import channelCodes from "text!templates/ChannelCodes.html"

/* @ngInject */
export default () => {
    return {
        scope: {
            promotion: '=promotion'
        },
        restrict: 'E',
        template: channelCodes,
        controller: 'channelCodesController',
        controllerAs: 'ctrl'
    };
}