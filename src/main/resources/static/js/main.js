$(document).ready(function () {
    $('#dealers_table').DataTable({
        'aoColumnDefs': [{
            'bSortable': false,
            'aTargets': [-1, -2]
        }]
    });
});

$(document).ready(function () {
    $('#orders_table_open_payoffs').DataTable({
        'aoColumnDefs': [{
            'bSortable': false,
            'aTargets': [-1, -2]
        }]
    });
});

$(document).ready(function () {
    $('#dealers_details_table').DataTable();
});