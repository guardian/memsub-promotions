import template from "text!templates/PreviewPromotion.html"

/* @ngInject */
export default () => {
    return {
        restrict: 'E',
        controller: 'previewPromotionController',
        template: template,
        scope: {
            'promotion': '=',
            'validity': '<'
        },
        link: function (scope, element, attr, ctrl) {
            
            scope.src = '';
            scope.$watch('validity', function() {
                ctrl.promotionUpdated(scope.promotion, scope.validity);
            });
            
            scope.$watch('promotionString', function() {
                let form = element[0].querySelector('form');
                form.submit();
            });
            
            scope.$watch('promotion', function() {
                ctrl.promotionUpdated(scope.promotion, scope.validity);
            }, true);
        }
    };
}