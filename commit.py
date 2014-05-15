# coding=utf8
"""
A simple script that commits ALL changes to a repository
 
Copyright © 2014 Dimitri Molenaars <tyrope@tyrope.nl>
This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.
"""
 
conf = {}
#----------------------
#- User Configuration -
#----------------------
# Verbosity settings
conf['print_stdout'] = True # Print git output to console.
conf['print_stderr'] = True # Print git errors to console.
conf['silent'] = False # Silence non-git output.
 
# Push settings
conf['push'] = True # Whether or not to git push.
                    # If False, this program will only commit.
conf['remote'] = 'origin' # Remote repository you want to push to.
conf['branch'] = 'master' # Remote branch you want to push to.
#------------------------
#- End of Configuration -
#------------------------
 
import time
from time import sleep
from datetime import datetime
import subprocess
 
#Prepare commands & Counter value
gitadd = ['git', 'add', '.']
gitcommit = ['git', 'commit','-a','-m']
gitpush = ['git', 'push', conf['remote'], conf['branch']]
gitstatus = ['git', 'status']
counter = 1
 
#Helper method.
def gitcmd(cmd):
        # Open up a proccess
        p = subprocess.Popen(cmd, stdout = subprocess.PIPE,
            stderr = subprocess.PIPE)
 
        # Talk to me.
        out, err = p.communicate()
 
        # Thank you.
        if err != '' and conf['print_stderr']:
            print err
        if conf['print_stdout']:
            print out
        return out
 
def getNew():
    status = gitcmd(gitstatus).split("\n")
    return [x[14:] for x in status if x.startswith("#\tnew file:   ")]
 
def getModified():
    status = gitcmd(gitstatus).split("\n")
    return [x[14:] for x in status if x.startswith("#\tmodified:   ")]
 
def getDeleted():
    status = gitcmd(gitstatus).split("\n")
    return [x[14:] for x in status if x.startswith("#\tdeleted: ")]
 
 
 
# Main Cycle start
if not conf['silent']:
    print "Starting the auto committer in 3, 2, 1, GO!"
 
while True:
    counter -= 1
    if counter > 0:
        # Wait...
        if not conf['silent']:
            print "Pushing to remote in %s minutes." % (counter+1,)
    else:
        # Add
        gitcmd(gitadd)
        
        new = getNew()
        modified = getModified()
        deleted = getDeleted()
 
        # Commit
        if not conf['silent']:
            print "Committing"
        gitcmd(gitcommit + ['AutoCommit: %s. %d deletions, %d adds, %d modifications' % (time.strftime("%Y-%m-%d %H:%M:%S"), len(deleted), len(new), len(modified))])
 
        # Push
        if conf['push']:
            if not conf['silent']:
                print "Pushing."
            gitcmd(gitpush)
 
        if not conf['silent']:
            print "Push complete. Starting new 15 minutes timer."
        counter = 2
 
    sleep(60)