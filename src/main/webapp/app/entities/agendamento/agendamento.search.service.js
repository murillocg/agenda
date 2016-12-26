(function() {
    'use strict';

    angular
        .module('agendaApp')
        .factory('AgendamentoSearch', AgendamentoSearch);

    AgendamentoSearch.$inject = ['$resource'];

    function AgendamentoSearch($resource) {
        var resourceUrl =  'api/_search/agendamentos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
