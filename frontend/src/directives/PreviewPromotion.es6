/* @ngInject */
export default () => {
    return {
        restrict: 'E',
        controller: 'previewPromotionController',
        template: '<iframe flex ng-src="{{src}}" frameborder="0"/>',
        scope: {
            'promotion': '='
        },
        link: function (scope, element, attr, ctrl) {
            scope.src = '';
            scope.$watch('promotion', function(newPromo) {
                ctrl.promotionUpdated(newPromo);
            }, true);
        }
    };
}