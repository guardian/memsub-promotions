export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService) {
        this.service = promotionService;
        this.$scope = $scope;
        this.fetchPromotion($stateParams.uuid);
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
        this.service.save(this.removeEmptyCodes(promotion));
    }
}
