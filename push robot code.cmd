set /p commit="Commit message: "
git add *
git commit -m "%commit%"
git push origin master