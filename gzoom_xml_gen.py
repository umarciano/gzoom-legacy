touch gzoom_xml_gen.py

import sys 
from datetime import datetime

def generate_file(path, version, cd):
    basename = "project.info"
    f = open(path+"/"+basename, "w")
    f.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
    f.write("<info>\n")
    f.write("<version>{}</version>\n".format(version))
    f.write("<date>{}</date>\n".format(cd))
    f.write("</info>\n")
    f.close()

if __name__ == "__main__": 
    cd = datetime.today().strftime('%Y-%m-%dT%H:%M:%S.000000Z')
    generate_file("hot-deploy/base/config",sys.argv[1],cd)
    generate_file("hot-deploy/custom/config",sys.argv[2],cd)
