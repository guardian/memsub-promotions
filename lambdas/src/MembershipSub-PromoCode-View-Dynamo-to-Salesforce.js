'use strict';

import { DynamoDBDocument } from "@aws-sdk/lib-dynamodb";
import { DynamoDB } from "@aws-sdk/client-dynamodb";
import {
    SecretsManagerClient,
    GetSecretValueCommand,
} from "@aws-sdk/client-secrets-manager";
import * as https from "https";
import * as querystring from "querystring";

const docClient = DynamoDBDocument.from(new DynamoDB());

function enquote(anArray) {
    return `"${anArray.join('","')}"`;
}

function generateCSVPromise(data) {
    const sfFieldNames = ['Name', 'Promotion_Name__c', 'Campaign_Name__c', 'Channel_Name__c', 'Product_Family__c', 'Promotion_Type__c', 'Discount_Percent__c', 'Discount_Months__c'];
    const fieldsToExport = ['promo_code', 'promotion_name', 'campaign_name', 'channel_name', 'product_family', 'promotion_type', 'discount_percent', 'discount_months'];
    const CSVData = [enquote(sfFieldNames)].concat(data.Items.map(record => enquote(fieldsToExport.map(field => record[field]))));
    return Promise.resolve(CSVData.join('\n'));
}

function makeSalesforceAPIRequest(options, requestBody, onEnd, onReqError) {
    console.log(`Making ${options.method} request to https://${options.hostname}${options.path}`);
    const req = https.request(options, (res) => {
        let body = '';
        res.on('data', (chunk) => body += chunk);
        res.on('end', () => onEnd(res, body));
    });
    req.on('error', onReqError);
    req.write(requestBody);
    req.end();
}

function login(config) {
    const loginData = {
        grant_type: 'password',
        client_id: config.client_id,
        client_secret: config.client_secret,
        username: config.username,
        password: config.password,
    }
    const body = querystring.stringify(loginData);
    const options = {
        hostname: config.salesforce_url,
        port: 443,
        path: '/services/oauth2/token',
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Content-Length': Buffer.byteLength(body)
        }
    };
    return new Promise((fulfilled, rejected) => {
        makeSalesforceAPIRequest(
            options,
            body,
            (res, body) => {
                if (res.statusCode === 200) {
                    const data = JSON.parse(body);
                    const accessToken = data.access_token;
                    const instanceUrl = data.instance_url.replace('https://', '');
                    fulfilled({ accessToken, instanceUrl });
                } else {
                    rejected(`Error: login-${res.statusCode} - ${body}`);
                }
            },
            rejected
        );
    });
}

// https://developer.salesforce.com/docs/atlas.en-us.api_bulk_v2.meta/api_bulk_v2/reference.htm
function makeAPICalls(csvData) {
    return (loginData) => {
        const accessToken = loginData.accessToken;
        const instanceUrl = loginData.instanceUrl;
        let interval = null;
        const getOptions = () => {
            return {
                hostname: instanceUrl,
                port: 443,
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                }
            };
        }
        function createAJob() {
            return new Promise((fulfilled, rejected) => {
                const options = getOptions();
                options.path = '/services/data/v42.0/jobs/ingest';
                options.method = 'POST';
                makeSalesforceAPIRequest(
                    options,
                    JSON.stringify({
                        "externalIdFieldName": "Name",
                        "object": "Promotion_Code__c",
                        "operation": "upsert"
                    }),
                    (res, body) => {
                        if (res.statusCode === 200) {
                            const data = JSON.parse(body);
                            fulfilled(data.id);
                        } else {
                            rejected(`Error: createAJob - ${res.statusCode} - ${body}`);
                        }
                    },
                    rejected
                );
            });
        }
        function uploadJobData(jobId) {
            return new Promise((fulfilled, rejected) => {
                const options = getOptions();
                options.path = `/services/data/v42.0/jobs/ingest/${jobId}/batches`;
                options.headers["Content-Type"] = 'text/csv';
                options.method = 'PUT';
                makeSalesforceAPIRequest(
                    options,
                    csvData,
                    (res, body) => {
                        if (res.statusCode === 201) {
                            fulfilled(jobId);
                        } else {
                            rejected(`Error: uploadJobData - ${res.statusCode} - ${body}`);
                        }
                    },
                    rejected
                );
            });
        }

        function closeAJob(jobId) {
            return new Promise((fulfilled, rejected) => {
                const options = getOptions();
                options.path = `/services/data/v42.0/jobs/ingest/${jobId}`;
                options.method = 'PATCH';
                makeSalesforceAPIRequest(
                    options,
                    JSON.stringify({ 'state': 'UploadComplete' }),
                    (res, body) => {
                        if (res.statusCode === 200) {
                            const data = JSON.parse(body);
                            if (data.state === 'UploadComplete') {
                                fulfilled(jobId);
                            } else {
                                rejected(`Error: closeAJob - ${res.statusCode} - ${body}`);
                            }
                        } else {
                            rejected(`Error: closeAJob - ${res.statusCode} - ${body}`);
                        }
                    },
                    rejected
                );
            });
        }
        function testJobHasCompleted(jobId, fulfilled, rejected) {
            const options = getOptions();
            options.path = `/services/data/v42.0/jobs/ingest/${jobId}`;
            options.method = 'GET';
            return () => makeSalesforceAPIRequest(
                options,
                '',
                (res, body) => {
                    if (res.statusCode === 200) {
                        const data = JSON.parse(body);
                        if (data.state === 'JobComplete') {
                            clearInterval(interval);
                            fulfilled(jobId);
                        } else if (data.state === 'Failed') {
                            clearInterval(interval);
                            rejected(data.errorMessage);
                        }
                    } else {
                        clearInterval(interval);
                        rejected(`Error: testJobHasCompleted - ${res.statusCode} - ${body}`);
                    }
                },
                (error) => {
                    clearInterval(interval);
                    rejected(error);
                }

            );
        }
        function waitForJobToComplete(jobId) {
            return new Promise((fulfilled, rejected) => {
                interval = setInterval(testJobHasCompleted(jobId, fulfilled, rejected), 250);
            });
        }

        function deleteAJob(jobId) {
            return new Promise((fulfilled, rejected) => {
                const options = getOptions();
                options.path = `/services/data/v42.0/jobs/ingest/${jobId}`;
                options.method = 'DELETE';
                makeSalesforceAPIRequest(
                    options,
                    '',
                    (res, body) => {
                        if (res.statusCode === 204) {
                            fulfilled(`Successfully updated Salesforce Promotion Codes`);
                        } else {
                            rejected(`Error: deleteAJob - ${res.statusCode} - ${body}`);
                        }
                    },
                    rejected
                );
            });
        }


        return createAJob()
            .then(uploadJobData)
            .then(closeAJob)
            .then(waitForJobToComplete)
            .then(deleteAJob)
    };
}

async function fetchConfig(stage) {
    const client = new SecretsManagerClient({
        region: "eu-west-1",
    });

    const SecretId = `${stage}/Salesforce/User/PromoCodeLambda`;

    return client.send(new GetSecretValueCommand({
        SecretId,
    })).then(response => {
        const config = JSON.parse(response.SecretString)
        console.log('Fetched config from Secrets Manager');
        return config;
    }).catch(err => {
        return Promise.reject(`Failed to fetch config with ID: ${SecretId}. Error was: ${err}`);
    });
}

export const handler = (event, context, callback) => {

    const TOUCHPOINT_BACKEND = /PROD$/.test(context.functionName) ? 'PROD' : 'CODE';
    const TableName = `MembershipSub-PromoCode-View-${TOUCHPOINT_BACKEND}`;

    fetchConfig(TOUCHPOINT_BACKEND).then(config => {
        docClient.scan({ TableName })
            .then(generateCSVPromise)
            .then(csvData =>
                login(config)
                    .then(makeAPICalls(csvData))
                    .then(successMessage => callback(null, `${successMessage} from table ${TableName}`))
            )
            .catch(callback);
    });
};
