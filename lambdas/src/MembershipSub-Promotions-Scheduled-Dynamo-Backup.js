const AWS = require('aws-sdk');

const ddb = new AWS.DynamoDB();

function createTodaysBackup(TableName) {
    return ddb.createBackup({
        'BackupName': `${TableName}-Scheduled-Backup`,
        TableName
    })
        .promise()
        .then(possiblyJustCreatedBackup => {
            console.log(`Created backup of table ${TableName}: ${possiblyJustCreatedBackup.BackupDetails.BackupArn}`);
            return possiblyJustCreatedBackup.BackupDetails.BackupArn;
        });
}

function deleteBackupsOlderThanXDays(TableName, retentionDays) {
    return (latestBackupARN) => {
        const unixtime = Math.floor(Date.now() / 1000);
        const retentionSeconds = (retentionDays * 60 * 60 * 24);
        const TimeRangeUpperBound = unixtime - retentionSeconds;

        return ddb.listBackups({
            TableName,
            TimeRangeUpperBound
        })
            .promise()
            .then(oldBackups => {

                const backupsToDelete = oldBackups.BackupSummaries.map(backupSummary => backupSummary.BackupArn).filter(backupArn => latestBackupARN !== backupArn)

                console.log(`${backupsToDelete.length} old backups for table: ${TableName} will be deleted...`);

                const deletionPromises = backupsToDelete.map(BackupArn => {
                    console.log(`Attempting to delete backup: ${BackupArn}`);
                    return ddb.deleteBackup({ BackupArn }).promise();
                });

                return Promise.all(deletionPromises).then(results => {
                    results.forEach(result => {
                        console.log(`Successfully deleted ${result.BackupDescription.BackupDetails.BackupArn}`)
                    });
                    console.log(`Successfully deleted ${results.length} backups for table: ${TableName}`);
                    return `Successfully${latestBackupARN ? ' created new backup and' : ''} deleted ${results.length} backups for table: ${TableName}`;
                })
            });
    }
}

exports.handler = (event, context, callback) => {

    const TOUCHPOINT_BACKEND = /PROD$/.test(context.functionName) ? 'PROD' : 'UAT';
    const RETENTION_DAYS = 14; // a number < 1 will delete everything except the backup `createTodaysBackup` just created

    const tablesToBackup = [
        `MembershipSub-Campaigns-${TOUCHPOINT_BACKEND}`,
        `MembershipSub-Promotions-${TOUCHPOINT_BACKEND}`
    ];
    Promise.all(tablesToBackup.map(tableName => createTodaysBackup(tableName).then(deleteBackupsOlderThanXDays(tableName, RETENTION_DAYS))))
        .then((results) => {
            callback(null, `Backup report: ${results.join('; ')}`)
        })
        .catch(callback)

};