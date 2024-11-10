/**
 * Handles data returned by API, read jsonObject, and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleMetadataResult(resultData) {
    let metadataTableBodyElement = jQuery("#metadata_table_body");
    metadataTableBodyElement.empty(); // Clear any existing content

    console.log("Iterating through metadata result.");

    // Iterate through resultData
    for (let tableName in resultData) {
        if (resultData.hasOwnProperty(tableName)) {
            // append table name
            let tableNameRow = `<h1 style="text-align: left;">${tableName}</h1>`;
            metadataTableBodyElement.append(tableNameRow);

            // append table column names
            let columnHeaderRow = `
                <tr>
                    <th>Attribute</th>
                    <th>Type</th>
                </tr>`;
            metadataTableBodyElement.append(columnHeaderRow);

            // append each column name, type, & size for each table
            resultData[tableName].forEach(column => {
                let rowHTML = `
                    <tr>
                        <td>${column["columnName"]}</td>
                        <td>${column["type"]}(${column["size"]})</td>
                    </tr>`;
                metadataTableBodyElement.append(rowHTML);
            });
        }
    }
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetadataResult(resultData)
});