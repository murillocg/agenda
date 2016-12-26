(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('ServicoController', ServicoController);

    ServicoController.$inject = ['$scope', '$state', 'Servico', 'ServicoSearch'];

    function ServicoController ($scope, $state, Servico, ServicoSearch) {
        var vm = this;

        vm.servicos = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Servico.query(function(result) {
                vm.servicos = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ServicoSearch.query({query: vm.searchQuery}, function(result) {
                vm.servicos = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
