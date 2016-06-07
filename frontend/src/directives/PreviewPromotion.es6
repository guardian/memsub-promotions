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
            
            scope.$watch('validity', function() {
                ctrl.promotionUpdated(scope.promotion, scope.validity);
            });
            
            let form = element[0].querySelector('form');
            scope.$watch('promotionString', function(newValue) {
                if(newValue) form.submit();
            });
            
            scope.$watch('promotion', function() {
                ctrl.promotionUpdated(scope.promotion, scope.validity);
            }, true);
        }
    };
}