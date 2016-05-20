/* @ngInject */
export default () => {
    return {
        restrict: 'A',
        link: function (scope, element, attr) {
            var refreshMe = function () {
                element.attr('src', element.attr('refreshable-src'));
            };

            scope.$watch('refreshIframe', function (newVal, oldVal) {
                if (scope.refreshIframe) {
                    scope.refreshIframe = false;
                    refreshMe();
                }
            });
        }
    };
}