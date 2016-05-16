/* @ngInject */
export default () => {
    return {
        restrict: 'A',
        scope: true,
        link: (scope, elem, attrs) => {

            let parts = attrs.deleteEmpty.split('.');
            let variable = parts.pop();
            let container = parts.join('.');

            scope.$watch(attrs.deleteEmpty, (n) => {
                elem.val(n)
            });

            elem.bind("keyup", () => {
                let n = elem.val();
                if (!n) {
                    let c = scope.$eval(container);
                    delete c[variable];
                    scope.$eval(container + ' = b', {b: c});
                } else {
                    scope.$eval(container + '[key] = n', {key: variable, n: n});
                }
                scope.$apply()
            });
        }
    };
}