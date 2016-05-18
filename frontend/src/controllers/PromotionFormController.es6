const emptyPromotion = {
  appliesTo: {
    productRatePlanIds: [],
    countries: []
  },
  codes: {},
  promotionType: { name: "tracking" },
  landingPage: {}
};

export default class {

    /* @ngInject */
    constructor($state, $stateParams, $scope, promotionService, uuid) {
        this.service = promotionService;
        this.$scope = $scope;
        this.$state = $state;
        this.uuid = uuid;
        
        if ($stateParams.uuid) {
            this.fetchPromotion($stateParams.uuid);
        } else if ($stateParams.campaignCode) {
            this.createNewPromotion($stateParams.campaignCode);
        } else {
            // Later
        }
    }

    createNewPromotion(campaignCode) {
        this.$scope.promotion = Object.assign({}, emptyPromotion, {campaignCode: campaignCode, uuid: this.uuid.v4()});
    }

    fetchPromotion(uuid) {
        let self = this;
        this.service.get(uuid)
            .then(this.transformDates.bind(self))
            .then(p => this.$scope.promotion = p);
    }

    transformDates(promotion) {
        let expires = promotion.expires ? {expires: new Date(promotion.expires)} : {};
        return Object.assign({}, promotion, {starts: new Date(promotion.starts)}, expires)
    }

    removeEmptyCodes(promotion) {
        let promoCopy = Object.assign({}, promotion);
        promoCopy.codes = Object.keys(promoCopy.codes).map(channel => {
            let newChannel = {};
            newChannel[channel] =promoCopy.codes[channel].filter(code => code != "");
            return newChannel
        }).reduce((acc, n) => Object.assign(acc, n), {});
        return promoCopy;
    }

    save(promotion) {
        this.$scope.saving = true;
        this.service.save(this.removeEmptyCodes(promotion)).then(() =>
            this.$state.go('allPromotions.singleCampaign', {code: promotion.campaignCode})
        );
    }
}
