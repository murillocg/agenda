(function() {
    'use strict';
    angular
        .module('agendaApp')
        .factory('Agendamento', Agendamento);

    Agendamento.$inject = ['$resource', 'DateUtils'];

    function Agendamento ($resource, DateUtils) {
        var resourceUrl =  'api/agendamentos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dataHoraInicio = DateUtils.convertDateTimeFromServer(data.dataHoraInicio);
                        data.dataHoraFim = DateUtils.convertDateTimeFromServer(data.dataHoraFim);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
