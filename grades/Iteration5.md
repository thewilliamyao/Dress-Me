# Iteration 5 Evaluation - Group 5

**Evaluator: [Xu, Jizhou](mailto:jxu55@jhu.edu)**

## Progress Comments -- on target for iteration 6?
You guys are a little behind on progress. Some features are still not fully integrated (i.e. only frontend).
### Size of codebase -- looking reasonable? (-4)
Yes, but since you're a team of 5 and this project is semester-long, I would say you could've done a bit more.
## Code Inspection

### non-CRUD feature code inspection (-4)
Like I mentioned before, the getRecommendation mechanism still looks like a bunch of simple queries stacked together, and there's no point in having top 3 recommendations if they going to be the same most of the time. Although I don't think you have enough time left for a huge improvement, I think this app can have a recommendation engine based on whether the users liked or disliked the outfit generated by the app. The idea is similar to the one in [this article](https://www.toptal.com/algorithms/predicting-likes-inside-a-simple-recommendation-engine).
### Package structure of code and other high-level organization aspects
Seems ok.
### Code inspection for bad smells anti-patterns, etc (-2)
There isn't a lot of refactoring done for the backend. Some classes such as "Weather" are a bit data centric (i.e. if the only purpose of the class is to hold the information returned from your API call, it's not really meaningful to have such a class), but since you might have reasons to use POJOs for better JSON handling, I'm not penalizing you hard here.
## Build / run / test / deploy
Seems ok.
## Github
Resolve and close all those issues... please...
## Iteration Plan / Docs (-2)
"More requests from frontend to backend and displaying the information" is vague. Also, just another heads-up, Iteration 5 was supposed to be the alpha release of your project, and from the iteration requirement website I quote, "you really should have your core app working for this iteration".
## Overall Comments
Utilize these last few days to really finish and polish your project. You can still have a good demo on your final presentation, and it really counts.

**Grade: 88/100**
