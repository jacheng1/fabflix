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