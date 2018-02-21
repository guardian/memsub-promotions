'use strict';

const fs = require('fs');
const AWS = require('aws-sdk');

Array.prototype.flatMap = function(lambda) {
    return Array.prototype.concat.apply([], this.map(lambda));
};

function generatePutRequest(campaignsByCode, channelName, promotion, tableName) {
    return (promoCodeObj) => {
        const campaignCode = promotion.campaignCode.S;
        const promotionName = promotion.name.S;
        if (!promotionName) {
            return;
        }
        if (!campaignCode) {
            return;
        }
        const campaignData = campaignsByCode[campaignCode];
        if (!campaignData) {
            return;
        }
        const promotionType = promotion.promotionType;
        if (!(promotionType && promotionType.M && promotionType.M.name)) {
            return;
        }
        const promotionTypeName = promotionType.M.name.S;
        let discountPercent = 0;
        let discountDurationMonths = 0;
        if (promotionTypeName === 'percent_discount') {
            if (promotionType.M.amount) {
                discountPercent = parseInt(promotionType.M.amount.N, 10);
            }
            if (promotionType.M.durationMonths) {
                discountDurationMonths = parseInt(promotionType.M.durationMonths.N, 10);
            }
        }

        const promoCode = promoCodeObj.S;
        if (!promoCode) {
            return;
        }

        return {
            TableName: tableName,
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
        };
    };
}

function generatePutRequests(campaignsByCode, promotions, tableName) {
    return promotions.flatMap(promotion => {
        const codesObj = promotion.codes.M;
        const channelNames = Object.keys(codesObj);
        return channelNames.flatMap(channelName => {
            const promoCodes =  codesObj[channelName].L;
            return promoCodes.map(generatePutRequest(campaignsByCode, channelName, promotion, tableName));
        });
    });
}

function collateCampaigns(campaigns) {
    const campaignDetailsByCampaignCode = {};
    campaigns.forEach(campaign => {
        campaignDetailsByCampaignCode[campaign.code.S] = {
            campaign_name: campaign.name.S,
            product_family: (campaign.group || campaign.productFamily).S // legacy
        }
    });
    return campaignDetailsByCampaignCode;
}

function rapidWritePutRequestsIntoTable(putRequests) {
    const DOC = require('dynamodb-doc');
    const docClient = new DOC.DynamoDB();

    console.log(`Attempting to update ${putRequests.length} promo code views`);

    const promises = putRequests.map(putRequest => docClient.putItem(putRequest).promise());

    return Promise.all(promises).then(responses => {
        console.log(`Updated ${responses.length} promo code views`)
    });
}


exports.local = (event, context, callback) => {
    // lambda-local -l MembershipSub-Reconstruct-PromoCode-View.js -h local -e examples/local.js

    fs.statSync('campaigns.json');
    fs.statSync('promotions.json');

    const campaigns = fs.readFileSync('campaigns.json', { encoding: 'UTF-8' }).trim().split('\n').map(JSON.parse);
    const campaignsByCode = collateCampaigns(campaigns);
    const promotions = fs.readFileSync('promotions.json', { encoding: 'UTF-8' }).trim().split('\n').map(JSON.parse);
    const putRequests = generatePutRequests(campaignsByCode, promotions, event.tableToUpdate);
    if (event.tableToUpdate) {
        rapidWritePutRequestsIntoTable(putRequests).then(callback);
    } else {
        callback(null, putRequests);
    }
};

exports.handler = (event, context, callback) => {
    const ddb = new AWS.DynamoDB();

    const source = /PROD$/.test(context.functionName) ? 'PROD' : 'UAT';

    const campaignsP = ddb.scan({ TableName: `MembershipSub-Campaigns-${source}`}).promise();
    const promotionsP = ddb.scan({ TableName: `MembershipSub-Promotions-${source}`}).promise();

    campaignsP.then(campaigns => {
        return promotionsP.then(promotions => {
            console.log(`Got ${campaigns.Items.length} campaigns and ${promotions.Items.length} promotions from raw data sources`);
            const putRequests = generatePutRequests(collateCampaigns(campaigns.Items), promotions.Items, `MembershipSub-PromoCode-View-TEST`);
            const validatedPutRequests = putRequests.filter(putRequest => !!(putRequest && putRequest.Item));
            console.log(`Created ${putRequests.length} PutRequets. ${validatedPutRequests.length} are valid`);
            return validatedPutRequests;
        })
    })
        .then(rapidWritePutRequestsIntoTable)
        .then(callback)
        .catch(callback);
};