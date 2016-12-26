(function() {
    'use strict';
    angular
        .module('agendaApp')
        .factory('Servico', Servico);

    Servico.$inject = ['$resource'];

    function Servico ($resource) {
        var resourceUrl =  'api/servicos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
