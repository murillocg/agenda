(function() {
    'use strict';

    angular
        .module('agendaApp')
        .factory('ClienteSearch', ClienteSearch);

    ClienteSearch.$inject = ['$resource'];

    function ClienteSearch($resource) {
        var resourceUrl =  'api/_search/clientes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
