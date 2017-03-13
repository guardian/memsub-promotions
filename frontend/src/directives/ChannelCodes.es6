import channelCodes from "text!templates/ChannelCodes.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            codes: '='
        },
        restrict: 'E',
        template: channelCodes,
        controller: 'channelCodesController',
        controllerAs: 'ctrl',
        link: (scope, elem, attrs, controller) => {
            scope.$watch('codes', (n) => {
                if (!scope.updating && n) {
                    scope.updating = true;
                    controller.populateChannels(n);
                }
                setTimeout(() => scope.updating = false, 0)

            },true);
            scope.$watch('channels', (n) => {
                if (!scope.updating && n) {
                    scope.updating = true;
                    controller.applyChannels(n);
                }
                setTimeout(() => scope.updating = false, 0)
            },true);
        }
    };
}