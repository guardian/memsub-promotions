/* @ngInject */
export default ($mdDateLocaleProvider) => {
    $mdDateLocaleProvider.formatDate = date => {
        const formattedDate = date ? date.toLocaleDateString('en-GB') : '';
        return formattedDate;
    }

    $mdDateLocaleProvider.parseDate = date => {
        if (date) {
            const [day, month, year] = date.split("/");
            return new Date(year, month - 1, day);
        }
    }
}
