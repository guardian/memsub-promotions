/* @ngInject */
export default () => {
    return {
        restrict: 'E',
        controller: 'previewPromotionController',
        template: '<iframe flex ng-src="{{src}}" frameborder="0" ng-show="!invalid"></iframe><div layout-align="center center" layout="row" ng-show="invalid" class="preview-error" flex><span>Preview not yet available</span></div>',
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