export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService, ratePlanService, countryService) {
        this.service = promotionService;
        this.$scope = $scope;

        this.$scope.countryGroups = {};
        this.fetchPromotion($stateParams.uuid);
        this.countryGroups = countryService.all();
        this.ratePlans = ratePlanService.all();

        this.ratePlans.then(plans => $scope.ratePlans = plans);
        this.countryGroups.then(countries => $scope.countryGroups = countries);
    }

    fetchPromotion(uuid) {
        let self = this;
        let promo = this.service.get(uuid)
                        .then(this.transformDates.bind(self));

        promo.then(p => this.$scope.promotion = p);
        promo.then(this.populateRatePlans.bind(self));
        promo.then(this.populateCountryGroups.bind(self));
    }

    arrayToMap(array) {
        return array.reduce((obj, id) => {
            let newObject = {};
            newObject[id] = true;
            return Object.assign(obj, newObject);
        }, {});
    }

    populateRatePlans(promotion) {
        this.$scope.ratePlanIds = this.arrayToMap(promotion.appliesTo.productRatePlanIds)
    }

    transformDates(promotion) {
        let expires = promotion.expires ? {expires: new Date(promotion.expires)} : {};
        return Object.assign({}, promotion, {starts: new Date(promotion.starts)}, expires)
    }

    applyRatePlans(ratePlanIds) {
        this.$scope.promotion.appliesTo.productRatePlanIds = Object.keys(ratePlanIds).filter(id => ratePlanIds[id] == true)
    }

    populateCountryGroups(promotion) {
        this.countryGroups.then(countryGroups => {
            this.$scope.selectedCountryGroups = this.arrayToMap(promotion.appliesTo.countries.map(countryCode =>
                countryGroups.filter(cg => cg.countries.includes(countryCode))[0]).map(cg => cg.id));
        });
    }

    applyCountryGroups(countryGroupIds) {
        countryGroupIds = Object.keys(countryGroupIds).filter(id => countryGroupIds[id] == true);
        let selectedCountries = this.countryGroups.then(countryGroups => countryGroups
            .filter(group => countryGroupIds.includes(group.id))
            .map(group => group.countries)
            .reduce((arr, countries) => arr.concat(countries), [])
        );
        selectedCountries.then(countries => this.$scope.promotion.appliesTo.countries = countries);
    }

    save(promotion) {
        this.service.save(promotion);
    }
}
