export default class {

    /* @ngInject */
    constructor($scope, ratePlanService) {
        this.$scope = $scope;
        this.ratePlans = ratePlanService.all();
        this.ratePlans.then(plans => $scope.ratePlans = plans);
        $scope.discountedPrice = (plan)=> {
            let factor = $scope.length / plan.period;
            return plan.price * factor/Math.ceil(factor)
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

    test(){
        return "help me im angular js"
    }


}
