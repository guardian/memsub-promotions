function enquote(anArray) {
    return `"${anArray.join('","')}"`;
}

exports.handler = (event, context, callback) => {

    var AWS = require('aws-sdk');
    AWS.config.update({region: 'eu-west-1'});
    var ddb = new AWS.DynamoDB();
    var s3 = new AWS.S3();

    const source = /PROD$/.test(context.functionName) ? 'PROD' : 'TEST';
    const TableName = `MembershipSub-PromoCode-View-${source}`;
    const Bucket = 'ophan-raw-membership-promo-code-view';
    const Key = TableName + '.csv';
    const fieldsToExport = ['promo_code', 'promotion_name', 'campaign_name', 'channel_name', 'product_family'];

    ddb.scan({ TableName }, (err, data) => {
        if (err) return callback(err);

        const CSVData = [enquote(fieldsToExport)].concat(data.Items.map(record => enquote(fieldsToExport.map(field => record[field].S))));

        s3.putObject({
            Bucket, Key,
            ACL: 'bucket-owner-full-control',
            Body: CSVData.join('\n')
        }, (err2, data2) => callback(err2, `Successfully exported ${CSVData.length} records from ${TableName} to aws-ophan S3 file: s3://${Bucket}/${Key} (RAW Data Lake)`));
    });

};