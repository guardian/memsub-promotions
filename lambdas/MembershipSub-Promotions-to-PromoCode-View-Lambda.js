'use strict';

var AWS = require('aws-sdk'),
    DOC = require('dynamodb-doc');
var docClient = new DOC.DynamoDB();
var TOUCHPOINT_BACKEND = 'UAT';
var campaignDetailsByCampaignCode = {};
var putRequestsByPromoCode = {};
var promoCodesToUpdate = [];

function handleEventRecords(eventRecords) {
    if (!(eventRecords && eventRecords.length > 0)) { return; }

    eventRecords.forEach((record) => {
        if (!record.dynamodb) { return; }
        var newImage = record.dynamodb.NewImage;

        Object.keys(newImage.codes.M)
        .forEach((channelName) => {
            newImage.codes.M[channelName].L.forEach((promoCodeObj) => generatePutRequestFromDynamoPromoCodeObject(promoCodeObj, newImage, channelName));
        });
    });

    console.log(`Successfully processed ${eventRecords.length} records.`);
}

function generatePutRequestFromDynamoPromoCodeObject(promoCodeObj, newImage, channelName) {
    if (!(promoCodeObj && newImage.campaignCode && newImage.name)) { return; }

    var promoCode = promoCodeObj.S;
    if (!promoCode) { return; }
    var campaignCode = newImage.campaignCode.S;
    var promotionName = newImage.name.S;
    if (!promotionName) { return; }
    if (!campaignCode) { return; }
    var campaignData = campaignDetailsByCampaignCode[campaignCode];
    if (!campaignData) { return; }

    putRequestsByPromoCode[promoCode] = {
        PutRequest: {
            Item: {
                channel_name: channelName,
                promo_code: promoCode,
                campaign_code: campaignCode,
                promotion_name: promotionName,
                campaign_name: campaignData.campaign_name,
                product_family: campaignData.product_family
            }
        }
    };
}

function chunkedUpdateOfPromoCodes(callback) {
    var i, j, temparray, chunk = 25;

    promoCodesToUpdate = Object.keys(putRequestsByPromoCode);

    for (i = 0, j = promoCodesToUpdate.length; i < j; i += chunk) {
        temparray = promoCodesToUpdate.slice(i, i + chunk);
        if (!temparray || temparray.length === 0) continue;
        batchWriteRequestsForCodes(temparray, callback);
    }
}

function batchWriteRequestsForCodes(promoCodes, callback) {
    var putRequestsAsArray = [];

    promoCodes.forEach((key) => putRequestsAsArray.push(putRequestsByPromoCode[key]));

    console.log(`Putting records into table: MembershipSub-PromoCode-View-${TOUCHPOINT_BACKEND} = `, JSON.stringify(putRequestsAsArray, true));

    var RequestItemsObj = {};
    RequestItemsObj['MembershipSub-PromoCode-View-' + TOUCHPOINT_BACKEND] = putRequestsAsArray;

    docClient.batchWriteItem({
        RequestItems: RequestItemsObj
    })
    .promise()
    .then((data) => {
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

    // Not needed in node 4.3 EV
    // context.done('success', promoCodesToUpdate.length);
}

exports.handler = (event, context, callback) => {

    TOUCHPOINT_BACKEND = /PROD$/.test(context.functionName) ? 'PROD' : 'UAT';

    docClient.scan({
        TableName: 'MembershipSub-Campaigns-' + TOUCHPOINT_BACKEND
    })
    .promise()
    .then((data) => {
        console.log(`Gotten all ${data.Items.length} ${TOUCHPOINT_BACKEND} Campaigns`);
        data.Items.forEach((campaign) =>
            campaignDetailsByCampaignCode[campaign.code] = {
                campaign_name: campaign.name,
                product_family: campaign.group || campaign.product_family // legacy
            }
        );

        handleEventRecords(event.Records);

        chunkedUpdateOfPromoCodes(callback);

        attemptToComplete(callback);
    })
    .catch((err) => {
        console.error('error', err);
        attemptToComplete(callback);
    });
};