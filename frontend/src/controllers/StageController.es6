const STAGE = 'stage';

export default class {

    /* @ngInject */
    constructor($state, $scope, $cookies) {
        this.$scope = $scope;
        this.$state = $state;
        this.$cookies = $cookies;
        this.$scope.stage = this.$cookies.get(STAGE);
    }

    setStage(stage) {
        this.$scope.stage = stage;
        this.$cookies.put(STAGE, stage);
        this.$state.go('allPromotions', {}, {reload: true})
    }
}
