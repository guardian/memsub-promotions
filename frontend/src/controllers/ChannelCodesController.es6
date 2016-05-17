export default class {

    /* @ngInject */
    constructor($scope) {
        this.$scope = $scope;
    }

    addChannel(newChannelName) {
        this.$scope.channels.push({name: newChannelName, codes: []});
    }

    deleteChannel(currentChannels, channelName) {
        if (channelName) {
            this.$scope.channels = currentChannels.filter(c => c.name != channelName);
        }
    }

    addCode(currentChannels, channelName) {
        this.$scope.channels = currentChannels.map(c => {
            if (c.name == channelName) {
                c.codes.push("");
            }
            return c;
        });
    }

    deleteCode(currentChannels, channelName, code) {
        console.log(currentChannels, channelName, code);
        this.$scope.channels = currentChannels.map(c => {
            if (c.name == channelName) {
                if (c.codes.includes(code)) {
                    c.codes.splice(c.codes.indexOf(code), 1);
                }
            }
            return c;
        });
    }

    codeUpdated(currentChannels, channelName, code) {
        if (!code) {
            this.deleteCode(currentChannels, channelName, code);
        }
    }

    populateChannels(channels) {
        this.$scope.channels = Object.keys(channels).map(k => { return {"name": k, "codes": channels[k]} })
    }

    applyChannels(channels) {
        this.$scope.codes = channels.map(c => {
            let o = {};
            o[c.name] = c.codes;
            return o
        }).reduce((arr, codes) => Object.assign(arr, codes), {})
    }

}
