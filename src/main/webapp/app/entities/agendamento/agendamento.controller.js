(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('AgendamentoController', AgendamentoController);

    AgendamentoController.$inject = ['$scope', '$state', 'Agendamento', 'AgendamentoSearch'];

    function AgendamentoController ($scope, $state, Agendamento, AgendamentoSearch) {
        var vm = this;

        vm.agendamentos = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Agendamento.query(function(result) {
                vm.agendamentos = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AgendamentoSearch.query({query: vm.searchQuery}, function(result) {
                vm.agendamentos = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
