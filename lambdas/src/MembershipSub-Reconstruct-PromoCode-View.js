'use strict';

import { DynamoDB } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocument } from "@aws-sdk/lib-dynamodb";
import * as fs from 'fs';

/*
    This file can be uploaded to a Lambda function and used to do a full refresh of the MembershipSub-PromoCode-View-[STAGE] database.
    You just have to run the index.handler function inside a Lambda whose name ends with -PROD or -CODE depending on what stage's MembershipSub-PromoCode-View- table you want to update.
*/

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
    const docClient = DynamoDBDocument.from(new DynamoDB());

    console.log(`Attempting to update ${putRequests.length} promo code views`);

    const promises = putRequests.map(putRequest => docClient.putItem(putRequest).promise());

    return Promise.all(promises).then(responses => {
        console.log(`Updated ${responses.length} promo code views`)
    });
}


export const local = (event, context, callback) => {
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

export const handler = (event, context, callback) => {
    /*
        dynamodb-doc is not used here because the builders to collate campaigns and generate put requests
        may need to work off  S3 DynamoDB local backup JSON files (above), which will contain the .S .L .N .M... type prefixes.
        Using the local test  also helps with testing the builders so that you don't have to run the Lambda 100s of times.
    */

    const ddb = new DynamoDB();

    const source = /PROD$/.test(context.functionName) ? 'PROD' : 'CODE';

    const campaignsP = ddb.scan({ TableName: `MembershipSub-Campaigns-${source}`});
    const promotionsP = ddb.scan({ TableName: `MembershipSub-Promotions-${source}`});

    Promise.all([campaignsP, promotionsP])
        .then(results => {
            const campaigns = results[0];
            const promotions = results[1];
            console.log(`Got ${campaigns.Items.length} campaigns and ${promotions.Items.length} promotions from raw data sources`);
            const putRequests = generatePutRequests(collateCampaigns(campaigns.Items), promotions.Items, `MembershipSub-PromoCode-View-${source}`);
            const validatedPutRequests = putRequests.filter(putRequest => !!(putRequest && putRequest.Item));
            console.log(`Created ${putRequests.length} PutRequets. ${validatedPutRequests.length} are valid`);
            return validatedPutRequests;
        })
        .then(rapidWritePutRequestsIntoTable)
        .then(callback)
        .catch(callback);
};
