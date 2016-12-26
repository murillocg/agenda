(function() {
    'use strict';

    angular
        .module('agendaApp')
        .controller('ClienteController', ClienteController);

    ClienteController.$inject = ['$scope', '$state', 'Cliente', 'ClienteSearch'];

    function ClienteController ($scope, $state, Cliente, ClienteSearch) {
        var vm = this;

        vm.clientes = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Cliente.query(function(result) {
                vm.clientes = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ClienteSearch.query({query: vm.searchQuery}, function(result) {
                vm.clientes = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
