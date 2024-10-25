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

    let genreHTML = '<div class="row">';
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
            genreHTML += '</div><div class="row">';
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
    dataType: "json", // set return data type
    method: "GET", // set request method
    url: "api/main?genre=" + genreId, // set request URL as mapped by MovieListServlet
    success: (resultData) => handleMainResult(resultData) // set callback function to handle returned data from MovieListServlet
});