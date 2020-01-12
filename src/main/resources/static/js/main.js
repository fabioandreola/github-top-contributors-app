var app = new Vue({
    el: '#app',
    data: {
        location: '',
        max_contributors_picked: '50',
        users: []
    },
    methods: {
        findContributors() {
            this.users = []
            fetch("http://localhost:8080/api/top-contributors?location=" + this.location + "&maxResults=" + this.max_contributors_picked)
                .then(response => response.json())
                .then((data) => {
                    if (data.isError) {
                        alert(data.errorMessage)
                    } else {
                        this.users = data.users;
                    }
                })
        }
    }
})
