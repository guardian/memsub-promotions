export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
    }

    addChannel(currentChannels, newChannelName) {
        currentChannels[newChannelName] = [];
        this.$scope.promotion.codes = currentChannels;
    }

    deleteChannel(currentChannels, channel) {
        if (channel) {
            delete currentChannels[channel];
            this.$scope.promotion.codes = currentChannels;
        }
    }

    addCode(currentChannels, channel, newCode) {
        if (newCode) {
            currentChannels[channel].push(newCode);
            this.$scope.promotion.codes = currentChannels;
            this.$scope.newCode = "";
        }
    }

    deleteCode(currentChannels, channel, code) {
        if (code) {
            if (currentChannels[channel].includes(code)) {
                currentChannels[channel].splice(currentChannels[channel].indexOf(code), 1);
                this.$scope.promotion.codes = currentChannels;
            }
        }
    }

}
