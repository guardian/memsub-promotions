/**
 * This is adapted from https://github.com/jadjoubran/angular-material-design-lite
 * which is currently only available via bower
 */

/* @ngInject */
export default $timeout => {
    return {
        restrict: 'A',
        compile:  () => {
            return {
                post: (scope, element) => $timeout(() => componentHandler.upgradeElements(element[0]), 0)
            }
        }
    };
}
