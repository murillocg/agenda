(function() {
    'use strict';

    angular
        .module('agendaApp')
        .factory('ServicoSearch', ServicoSearch);

    ServicoSearch.$inject = ['$resource'];

    function ServicoSearch($resource) {
        var resourceUrl =  'api/_search/servicos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
