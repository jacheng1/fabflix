/**
 * Retrieve parameter from obtained request URL, matched by parameter name
 * @param target
 * @returns {string|null}
 */
function getParameterByName(target) {
    let url = window.location.href; // retrieve request URL

    target = target.replace(/[\[\]]/g, "\\$&"); // encode target parameter name to URL encoding

    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"), results = regex.exec(url); // use regular expression to find matched parameter value

    if (!results) {
        return null;
    }

    if (!results[2]) {
        return '';
    }

    return decodeURIComponent(results[2].replace(/\+/g, " ")); // return decoded parameter value
}

/**
 * Handles data returned by API, read jsonObject, and populate data into HTML elements
 * @param resultData jsonObject
 */
function handleMainResult(resultData) {
    console.log("handleMainResult: populating genre info from resultData");
    console.log("resultData: ", resultData);

    let genreInfoElement = jQuery("#genre-links");

    let genreHTML = '<div class="container"><div class="row justify-content-center">';
    for (let i = 0; i < resultData.length; i++) {
        genreHTML += `
            <div class="genre-column col-md-3 text-center">
                    <p>
                        <a href="index.html?genre=${resultData[i]['genre_ids']}" class="custom-link">
                            ${resultData[i]["movie_genre"]}
                        </a>
                    </p>  
            </div>`;

        // check to end the current row and start a new one every 4 columns
        if ((i + 1) % 4 === 0 && i + 1 < resultData.length) {
            genreHTML += '</div><div class="row justify-content-center">';
        }
    }
    genreHTML += '</div>';

    genreInfoElement.append(genreHTML);
}

document.addEventListener("DOMContentLoaded", () => {
    // add links to alphabetical titles

    const alphaCharacters = [...'ABCDEFGHIJKLMNOPQRSTUVWXYZ']; // define the alphabetical characters A-Z
    const alphaLinksContainer = document.getElementById('alpha-links');

    alphaCharacters.forEach(char => {
        const link = document.createElement('a');
        link.href = `index.html?prefix=${char}`;
        link.innerText = char;
        link.style.marginRight = '10px';

        alphaLinksContainer.appendChild(link);
    })

    // add links to numerical titles
    const numericCharacters = [...'0123456789', '*']; // define the numeric characters 0-9 and the special character *
    const numericLinksContainer = document.getElementById('numeric-links');

    // loop through the characters array and create <a> tags for each character
    numericCharacters.forEach(char => {
        const link = document.createElement('a');
        link.href = `index.html?prefix=${char}`;
        link.innerText = char;
        link.style.marginRight = '10px';

        numericLinksContainer.appendChild(link);
    });
});

let genreId = getParameterByName("genre") || "";

// makes HTTP GET request; upon success, uses callback function handleMainResult()
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/main?genre=" + genreId,
    success: (resultData) => handleMainResult(resultData)
});

const handleLookup = (query, doneCallback) => {
    // Only trigger autocomplete for >=3 characters
    if (query.length < 3) {
        return;
    }

    console.log("Autocomplete initiated.");

    const cachedData = localStorage.getItem(query);
    if (cachedData) {
        console.log("Using cached results.");

        const parsedData = JSON.parse(cachedData);

        // Ensure cached data is correctly formatted
        const formattedSuggestions = parsedData.map((item) => ({
            value: String(item.value || item), // Enforce string value
            data: item.data || null
        }));

        handleLookupAjaxSuccess(formattedSuggestions, doneCallback);

        return;
    }

    console.log("Sending Ajax request to server...");

    jQuery.ajax({
        method: "GET",
        url: `api/autocomplete?full-text=${encodeURIComponent(query)}`,
        success: (data) => {
            localStorage.setItem(query, JSON.stringify(data));
            handleLookupAjaxSuccess(data, doneCallback);
        },
        error: (error) => {
            console.error("Autocomplete error:", error);
        },
    });
};

const handleLookupAjaxSuccess = (data, doneCallback) => {
    console.log("Raw autocomplete suggestion(s):", data);

    const suggestions = data.map((item) => ({
        value: String(item.value || item),
        data: item.data || null
    }));

    console.log("Formatted autocomplete suggestion(s):", suggestions);

    doneCallback({ suggestions });
};

const handleSelectSuggestion = (suggestion) => {
    window.location.href = `single-movie.html?id=${suggestion.data.id}`;
};

const handleSearch = (event) => {
    const query = $("#autocomplete").val();
    if (query.trim() === "") {
        alert("Please enter some text for searching.");
        event.preventDefault();
        return;
    }

    console.log("Full-text search query:", query);
};

$(document).ready(() => {
    $("#autocomplete").autocomplete({
        lookup: (query, doneCallback) => handleLookup(query, doneCallback),
        onSelect: (suggestion) => handleSelectSuggestion(suggestion),
        deferRequestBy: 300
    });

    $("#search-form").on("submit", handleSearch);
});