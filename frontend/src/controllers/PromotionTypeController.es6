export default class {

    /* @ngInject */
    constructor($scope, $timeout) {

        $timeout(() => {
            $scope.types = {
                incentive: "Incentive",
                free_trial: "Free trial",
                percent_discount: "Discount",
                tracking: "Tracking"
            };


        }, 0);


        this.$scope = $scope;
    }

    setPromotionType(oldName, newName) {
        if (oldName != newName) {
            this.$scope.promotion.promotionType = {name: newName}
        }
    }
}