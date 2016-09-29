export default class {

    /* @ngInject */
    constructor($scope, ratePlanService) {
        this.$scope = $scope;
        this.ratePlans = ratePlanService.all();
        this.ratePlans.then(plans => $scope.ratePlans = plans);
    }

    arrayToMap(array) {
        return array.reduce((obj, id) => {
            let newObject = {};
            newObject[id] = true;
            return Object.assign(obj, newObject);
        }, {});
    }

    populateRatePlans(productRatePlanIds) {
        this.$scope.ratePlanIds = this.arrayToMap(productRatePlanIds);
    }

    applyRatePlans(ratePlanIds) {
        this.$scope.productRatePlanIds = Object.keys(ratePlanIds).filter(id => ratePlanIds[id] == true)
    }

}
