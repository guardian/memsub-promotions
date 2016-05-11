export default class {

    /* @ngInject */
    constructor($stateParams, $scope, promotionService, ratePlanService) {
        this.service = promotionService;
        $scope.promotions = [];
        this.$scope = $scope;
        this.fetchPromotion($stateParams.uuid);
        ratePlanService.all().then(plans => $scope.ratePlans = plans.data);
    }

    fetchPromotion(uuid) {
        let promos = this.service.all().then(res => res.data);
        promos.then(r => this.$scope.promotion = r.filter(v => v.uuid == uuid)[0]);
    }

    save(promotion) {
        console.log(promotion, "is saved now");
    }
}
