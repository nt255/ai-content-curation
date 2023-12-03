# ai-content-curation
Automated AI Content Curation Pipeline

# Setup
1) Install Eclipse (or IntelliJ)
2) Install Lombok: Help menu > Install new software > Add https://projectlombok.org/p2 > Install the Lombok plugin and restart Eclipse
3) Do the same thing for other dependencies. Whenever adding a new dependency into pom.xml, `open pom.xml > Run > Run As > Maven Build > put "package" into goals and Run`. You'll see the jar for the dependency under Maven Dependencies in the project structure.
4) If indexes are out of sync (names are not found when it's clearly there), `Project > Clean`.
5) Run `Local Application`. Server and Processor can be run separately too.
     
# Eclipse Common Mistakes
- "The declared package does not match the expected package." highlighting in red.
	- close and reopen the window
	