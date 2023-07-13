'use strict';

import { DynamoDBDocument } from "@aws-sdk/lib-dynamodb";
import { DynamoDB } from "@aws-sdk/client-dynamodb";
import { S3 } from "@aws-sdk/client-s3";

const docClient = DynamoDBDocument.from(new DynamoDB());
const s3 = new S3();

function enquote(anArray) {
    return `"${anArray.join('","')}"`;
}

export const handler = (event, context, callback) => {

    const source = 'PROD'; // Only PROD PromoCode-View data goes to the Data Lake.

    const TableName = `MembershipSub-PromoCode-View-${source}`;
    const Bucket = 'ophan-raw-membership-promo-code-view';
    const Key = TableName + '.csv';
    const fieldsToExport = [
        'promo_code',
        'promotion_name',
        'campaign_name',
        'channel_name',
        'product_family',
        'promotion_type',
        'discount_percent',
        'discount_months'
    ];
    const ACL = 'bucket-owner-full-control';

    docClient.scan({ TableName })
    .then(data => {
        const CSVData = [enquote(fieldsToExport)].concat(data.Items.map(record => enquote(fieldsToExport.map(field => record[field]))));
        const Body = CSVData.join('\n');
        s3.putObject({ Bucket, Key, ACL, Body}, (err2, _) => {
            callback(err2, `Successfully exported ${CSVData.length} records from ${TableName} to aws-ophan S3 file: s3://${Bucket}/${Key} (RAW Data Lake)`)
        });
    })
    .catch(callback);

};
