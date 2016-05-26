/* @ngInject */
export default () => {
    return {
        restrict: 'E',
        controller: 'previewPromotionController',
        template: '<iframe flex ng-src="{{src}}" frameborder="0" ng-show="validity"></iframe><div layout-align="center center" layout="row" ng-show="!validity" class="preview-error" flex><span>Preview not yet available</span></div>',
        scope: {
            'promotion': '=',
            'validity': '<'
        },
        link: function (scope, element, attr, ctrl) {
            
            scope.src = '';
            scope.$watch('validity', function() {
                ctrl.promotionUpdated(scope.promotion, scope.validity);
            });
            
            scope.$watch('promotion', function() {
                ctrl.promotionUpdated(scope.promotion, scope.validity);
            }, true);
        }
    };
}