export default class {

    /* @ngInject */
    constructor($scope, environmentService) {
        this.$scope = $scope;
        this.environmentService = environmentService;
        this.$scope.campaignGroup = this.environmentService.getCampaignGroup();
        this.$scope.campaignGroupDomain = this.environmentService.getCampaignGroupDomain();
    }

    generateSuggestedPromoCode() {
        return this.environmentService.getCampaignGroupPrefix() + new Date().getTime().toString(36).toUpperCase();
    }

    addChannel(newChannelName) {
        this.$scope.channels.push({name: newChannelName, codes: []});
    }

    deleteChannel(currentChannels, channelName) {
        this.$scope.channels = currentChannels.filter(c => c.name != channelName);
    }

    addCode(currentChannels, channelName) {
        this.$scope.channels = currentChannels.map(c => {
            if (c.name == channelName) {
                c.codes.push(this.generateSuggestedPromoCode());
            }
            return c;
        });
    }

    deleteCode(currentChannels, channelName, code) {
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
        this.$scope.channels = Object.keys(channels).map(k => { 
            const promoCodes = channels[k];

            if (this.$scope.regenerateCodes) {
                const regeneratedPromoCodes = promoCodes.map((_, i) => `${this.generateSuggestedPromoCode()}${i}`);
    
                return {"name": k, "codes": regeneratedPromoCodes}; 
            }

            return {"name": k, "codes": promoCodes}; 
        })

        this.applyChannels(this.$scope.channels);
    }

    applyChannels(channels) {
        this.$scope.codes = channels.map(c => {
            let o = {};
            o[c.name] = c.codes;
            return o
        }).reduce((arr, codes) => Object.assign(arr, codes), {})
    }
}
