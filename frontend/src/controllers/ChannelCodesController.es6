export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
    }

    addChannel(currentChannels, newChannelName) {
        currentChannels[newChannelName] = [];
        this.$scope.promotion.codes = currentChannels;
    }

    addCode(currentChannels, channel, newCode) {
        if (newCode) {
            currentChannels[channel].push(newCode);
            this.$scope.promotion.codes = currentChannels;
            this.$scope.newCode = "";
        }
    }
}
