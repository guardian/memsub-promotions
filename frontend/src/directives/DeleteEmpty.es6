/* @ngInject */
export default () => {
    return {
        restrict: 'A',
        link: (scope, elem, attrs) => {
            let parts = attrs.ngModel.split('.');
            let variable = parts.pop();
            let container = parts.join('.');
            scope.$watch(attrs.ngModel, (n) => {
                if (!n) {
                    let c = scope.$eval(container);
                    delete c[variable];
                    scope.$eval(container + ' = b', {b: c});
                } else {
                    scope.$eval(container + '[key] = n', {key: variable, n: n});
                }
                setTimeout(() => scope.$apply, 0);
            });
        }
    };
}