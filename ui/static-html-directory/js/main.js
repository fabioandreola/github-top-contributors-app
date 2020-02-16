var app = new Vue({
    el: '#app',
    data: {
        location: '',
        max_contributors_picked: '50',
        users: [],
        loading: false
    },
    methods: {
        findContributors() {
            this.users = []
            this.loading = true
            fetch("/api/top-contributors?location=" + this.location + "&maxResults=" + this.max_contributors_picked)
                .then(response => response.json())
                .then((data) => {
                    this.loading = false
                    if (data.isError) {
                        alert(data.errorMessage)
                    } else {
                        this.users = data.users;
                    }
                })
        }
    },
    beforeMount() {
        fetch("/api", {
            method: 'GET',
            mode: 'no-cors'
        }).then(response => {
            if (response.status !== 404) {
                window.location = "/login";
            }
        })
    }
})
