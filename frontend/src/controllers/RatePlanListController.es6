export default class {

    /* @ngInject */
    constructor($scope, ratePlanService) {
        this.$scope = $scope;
        this.ratePlans = ratePlanService.all();
        this.ratePlans.then(plans => $scope.ratePlans = plans);
        $scope.discountedPrice = (plan)=> {
           //https://github.com/guardian/membership-common/blob/0cb919ea78a56e8307d51863d33788d46bd78ebd/src/main/scala/com/gu/memsub/promo/Promotion.scala#L270-L270
            let periodRatio = $scope.length?parseFloat($scope.length)/parseFloat(plan.period):1;
            let numberOfNewPeriods = Math.ceil(periodRatio);
            let newDiscountPercent = ($scope.discount * periodRatio) / numberOfNewPeriods;

            return plan.price * (1-newDiscountPercent/100);

        };
        $scope.showDiscountWarning = (plan) => {
           return (($scope.length % plan.period) !== 0)
        }
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

    updateDiscount(discount){
        this.$scope.discount = discount;
    }

    updateLength(length){
        this.$scope.length = length;
    }



}
