Vue.component('github-users', {
    props: ['users'],
    template: `
           <div class="github-users">
                <ul>
                    <li v-for="user in users">
                        <a v-bind:href="user.url" target="_blank">
                            <div class="message-avatar">
                                <img v-bind:src="user.avatarUrl" alt="."/>
                            </div>
                            <div class="message-body">
                                <div class="message-body-heading">
                                    <h5>{{user.username}}</h5>
                                    <span>{{user.publicRepositoryCount}} repositories</span>
                                </div>
                                <p></p>
                            </div>
                        </a>
                    </li>
                </ul>
            </div>
    `
});
