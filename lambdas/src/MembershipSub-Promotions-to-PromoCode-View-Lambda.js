'use strict';

const AWS = require('aws-sdk');
const DOC = require('dynamodb-doc');

const ddb = new AWS.DynamoDB();
const docClient = new DOC.DynamoDB(ddb);
const campaignDetailsByCampaignCode = {};
const putRequestsByPromoCode = {};

let promoCodesToUpdate = [];

function handleEventRecords(eventRecords) {
    if (!(eventRecords && eventRecords.length > 0)) { return; }

    eventRecords.forEach((record) => {
        if (!record.dynamodb) { return; }
        let newImage = record.dynamodb.NewImage;

        Object.keys(newImage.codes.M)
        .forEach((channelName) => {
            newImage.codes.M[channelName].L.forEach((promoCodeObj) => generatePutRequestFromDynamoPromoCodeObject(promoCodeObj, newImage, channelName));
        });
    });

    console.log(`Successfully processed ${eventRecords.length} records.`);
}

function generatePutRequestFromDynamoPromoCodeObject(promoCodeObj, newImage, channelName) {
    if (!(promoCodeObj && newImage.campaignCode && newImage.name)) { return; }

    const promoCode = promoCodeObj.S;
    if (!promoCode) { return; }
    const campaignCode = newImage.campaignCode.S;
    const promotionName = newImage.name.S;
    if (!promotionName) { return; }
    if (!campaignCode) { return; }
    const campaignData = campaignDetailsByCampaignCode[campaignCode];
    if (!campaignData) { return; }
    const promotionType = newImage.promotionType;
    if (!(promotionType && promotionType.M && promotionType.M.name)) { return; }
    const promotionTypeName = promotionType.M.name.S;
    let discountPercent = 0;
    let discountDurationMonths = 0;
    if (promotionTypeName === 'percent_discount') {
        discountPercent = parseInt(promotionType.M.amount.N, 10);
        discountDurationMonths = parseInt(promotionType.M.durationMonths.N, 10);
    }

    putRequestsByPromoCode[promoCode] = {
        PutRequest: {
            Item: {
                channel_name: channelName,
                promo_code: promoCode,
                campaign_code: campaignCode,
                promotion_name: promotionName,
                campaign_name: campaignData.campaign_name,
                product_family: campaignData.product_family,
                promotion_type: promotionTypeName,
                discount_percent: discountPercent,
                discount_months: discountDurationMonths
            }
        }
    };
}

function chunkedUpdateOfPromoCodes(callback, TOUCHPOINT_BACKEND) {
    let i, j, temparray = [];
    const chunk = 25;

    promoCodesToUpdate = Object.keys(putRequestsByPromoCode);

    for (i = 0, j = promoCodesToUpdate.length; i < j; i += chunk) {
        temparray = promoCodesToUpdate.slice(i, i + chunk);
        if (!temparray || temparray.length === 0) continue;
        batchWriteRequestsForCodes(temparray, callback, TOUCHPOINT_BACKEND);
    }
}

function batchWriteRequestsForCodes(promoCodes, callback, TOUCHPOINT_BACKEND) {
    const putRequestsAsArray = [];

    promoCodes.forEach((key) => putRequestsAsArray.push(putRequestsByPromoCode[key]));

    console.log(`Putting records into table: MembershipSub-PromoCode-View-${TOUCHPOINT_BACKEND} = `, JSON.stringify(putRequestsAsArray));

    const RequestItemsObj = {};
    RequestItemsObj['MembershipSub-PromoCode-View-' + TOUCHPOINT_BACKEND] = putRequestsAsArray;

    docClient.batchWriteItem({
        RequestItems: RequestItemsObj
    })
    .promise()
    .then(_ => {
        promoCodes.forEach((key) => delete putRequestsByPromoCode[key]);
        console.log(`Updated ${putRequestsAsArray.length} of ${promoCodesToUpdate.length} promo code views.`);
        attemptToComplete(callback);
    })
    .catch((err) => {
        console.error('error', err);
        attemptToComplete(callback);
    });
}

function attemptToComplete(callback) {
    if (Object.keys(putRequestsByPromoCode).length > 0) { return; }

    callback(null, `Updated all ${promoCodesToUpdate.length} promo code views.`);
}

exports.handler = (event, context, callback) => {

    const TOUCHPOINT_BACKEND = /PROD$/.test(context.functionName) ? 'PROD' : /UAT$/.test(context.functionName) ? 'UAT' : 'DEV';

    docClient.scan({
        TableName: 'MembershipSub-Campaigns-' + TOUCHPOINT_BACKEND
    })
    .promise()
    .then((data) => {
        console.log(`Gotten all ${data.Items.length} ${TOUCHPOINT_BACKEND} Campaigns`);
        data.Items.forEach((campaign) =>
            campaignDetailsByCampaignCode[campaign.code] = {
                campaign_name: campaign.name,
                product_family: campaign.group || campaign.productFamily // legacy
            }
        );

        handleEventRecords(event.Records);

        chunkedUpdateOfPromoCodes(callback, TOUCHPOINT_BACKEND);

        attemptToComplete(callback);
    })
    .catch((err) => {
        console.error('error', err);
        attemptToComplete(callback);
    });
};