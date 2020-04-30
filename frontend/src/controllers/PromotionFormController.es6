const emptyPromotion = {
  appliesTo: {
    productRatePlanIds: [],
    countries: []
  },
  codes: {},
  promotionType: { name: "tracking" }
};

export default class {

    /* @ngInject */
    constructor($state, $stateParams, $scope, promotionService, campaignService, environmentService, uuid, $q) {
        this.service = promotionService;
        this.campaignService = campaignService;
        this.environmentService = environmentService;
        this.$scope = $scope;
        this.$state = $state;
        this.uuid = uuid;
        this.$q = $q;
        this.$scope.serverErrors = [];
        this.$scope.campaignGroup = this.environmentService.getCampaignGroup();
        this.$scope.campaignGroupDomain = this.environmentService.getCampaignGroupDomain();

        if ($stateParams.uuid) {
            if ($stateParams.createPromotionCopy) {
                this.$scope.createPromotionCopy = true;
            } 
            this.fetchPromotion($stateParams.uuid);
        } else if ($stateParams.campaignCode) {
            this.createNewPromotion($stateParams.campaignCode);
        } else {
            // Later
        }
    }

    createNewPromotion(campaignCode) {
        const campaignCodeStub = {campaignCode: campaignCode, uuid: this.uuid.v4()};
        var landingPageStub = {landingPage: {type: this.environmentService.getCampaignGroup()}};
        this.$scope.promotion = Object.assign({}, emptyPromotion, campaignCodeStub, landingPageStub);
        
        return this.fillCampaignInfo(this.$scope.promotion)
    }

    fetchPromotion(uuid) {
        let self = this;
        this.service.get(uuid)
            .then(this.transformDates.bind(self))
            .then(this.fillCampaignInfo.bind(self))
            .then(p => {
                if (this.$scope.createPromotionCopy) {
                    return Object.assign({}, { ... p }, {
                        uuid: this.uuid.v4(),
                        name: `${p.name} [COPY]`,
                    });
                }

                return { ...p };
            })
            .then(p => this.$scope.promotion = p)
    }

    transformDates(promotion) {
        let expires = promotion.expires ? {expires: new Date(promotion.expires)} : {};
        return Object.assign({}, promotion, {starts: new Date(promotion.starts)}, expires)
    }

    fillCampaignInfo(promotion) {
        return this.campaignService.get(promotion.campaignCode)
            .then(c => promotion.campaignName = c.name) // overwrites deprecated legacy value on the promotion model
            .then(c => promotion)
    }

    removeEmptyCodes(promotion) {
        let promoCopy = Object.assign({}, promotion);
        promoCopy.codes = Object.keys(promoCopy.codes).map(channel => {
            let newChannel = {};
            newChannel[channel] = promoCopy.codes[channel].filter(code => code != "");
            return newChannel
        }).reduce((acc, n) => Object.assign(acc, n), {});
        return promoCopy;
    }

    update(promotion) {
        let sanitised = this.removeEmptyCodes(promotion);
        return this.service.save(sanitised)
    }

    save(promotion) {
        this.$scope.serverErrors = [];
        this.service.validate(promotion)
            .then(
                valid => this.update(valid),
                invalid => this.$scope.serverErrors = invalid
            )

    }

    close(promotion) {
        this.$scope.serverErrors = [];
        this.service.validate(promotion)
        .then(
            valid => this.update(valid).then(() => this.$state.go('allPromotions.singleCampaign', {code: promotion.campaignCode})),
            invalid => this.$scope.serverErrors = invalid
        )
            
    }
}
