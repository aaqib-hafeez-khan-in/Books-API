document.addEventListener('DOMContentLoaded', () => {
    const searchInput = document.getElementById('searchInput');
    const filterType = document.getElementById('filterType');
    const searchBtn = document.getElementById('searchBtn');
    const curlCommand = document.getElementById('curlCommand');
    const jsonResponse = document.getElementById('jsonResponse');
    const resultsSection = document.getElementById('results');
    const bookGrid = document.getElementById('bookGrid');
    const resultCount = document.getElementById('resultCount');
    const loader = document.getElementById('loader');

    const updateCurl = () => {
        const query = searchInput.value || '...';
        const type = filterType.value;
        const baseUrl = window.location.origin;
        let url = `${baseUrl}/api/books/search?`;
        
        if (type === 'all') {
            url += `query=${encodeURIComponent(query)}`;
        } else {
            url += `filterType=${type}&filterValue=${encodeURIComponent(query)}`;
        }

        curlCommand.innerHTML = `<span class="c-keyword">curl</span> -X GET \\\n  <span class="c-string">"${url}"</span>`;
    };

    const searchBooks = async () => {
        const query = searchInput.value.trim();
        if (!query) return;

        loader.classList.remove('hidden');
        searchBtn.disabled = true;
        searchBtn.textContent = 'Executing...';
        
        try {
            const type = filterType.value;
            let url = `/api/books/search?`;
            url += type === 'all' ? `query=${encodeURIComponent(query)}` : `filterType=${type}&filterValue=${encodeURIComponent(query)}`;

            const response = await fetch(url);
            const data = await response.json();
            
            jsonResponse.innerHTML = syntaxHighlight(data);
            
            displayResults(data);
        } catch (error) {
            jsonResponse.innerHTML = `<span class="c-keyword">Error:</span> <span class="c-string">Failed to fetch data</span>`;
        } finally {
            loader.classList.add('hidden');
            searchBtn.disabled = false;
            searchBtn.textContent = 'Execute Request';
        }
    };

    const syntaxHighlight = (json) => {
        if (typeof json !== 'string') {
            json = JSON.stringify(json, null, 2);
        }
        return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, (match) => {
            let cls = 'c-number';
            if (/^"/.test(match)) {
                if (/:$/.test(match)) {
                    cls = 'c-key';
                } else {
                    cls = 'c-string';
                }
            } else if (/true|false/.test(match)) {
                cls = 'c-keyword';
            } else if (/null/.test(match)) {
                cls = 'c-keyword';
            }
            return `<span class="${cls}">${match}</span>`;
        });
    };

    const displayResults = (data) => {
        bookGrid.innerHTML = '';
        resultsSection.classList.remove('hidden');
        
        if (!data.books || data.books.length === 0) {
            resultCount.textContent = '// No results found in the database';
            return;
        }

        resultCount.textContent = `// Found ${data.totalElements} records`;

        data.books.forEach(book => {
            const card = document.createElement('div');
            card.className = 'mini-card';
            
            const authors = book.authors && book.authors.length > 0 ? book.authors[0] : 'Unknown';
            const cover = book.coverUrl || 'https://via.placeholder.com/200x300?text=No+Cover';

            card.innerHTML = `
                <div class="mini-thumb">
                    <img src="${cover}" alt="${book.title}" onerror="this.src='https://via.placeholder.com/200x300?text=No+Cover'">
                </div>
                <div class="mini-content">
                    <h4 title="${book.title}">${book.title}</h4>
                    <p>${authors}</p>
                </div>
            `;
            bookGrid.appendChild(card);
        });
    };

    searchInput.addEventListener('input', updateCurl);
    filterType.addEventListener('change', updateCurl);
    searchBtn.addEventListener('click', searchBooks);
    searchInput.addEventListener('keypress', (e) => e.key === 'Enter' && searchBooks());
    
    updateCurl();
});
