(function() {
    'use strict';
    angular
        .module('agendaApp')
        .factory('Cliente', Cliente);

    Cliente.$inject = ['$resource', 'DateUtils'];

    function Cliente ($resource, DateUtils) {
        var resourceUrl =  'api/clientes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dataCadastro = DateUtils.convertDateTimeFromServer(data.dataCadastro);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
