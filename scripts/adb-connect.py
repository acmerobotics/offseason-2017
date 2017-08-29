from subprocess import run, Popen, PIPE, DEVNULL
from time import sleep
import sys, os

PROFILE_XML = '''<?xml version="1.0"?>
<WLANProfile xmlns="http://www.microsoft.com/networking/WLAN/profile/v1">
    <name>{ssid}</name>
    <SSIDConfig>
        <SSID>
            <name>{ssid}</name>
        </SSID>
    </SSIDConfig>
    <connectionType>ESS</connectionType>
    <connectionMode>auto</connectionMode>
    <MSM>
        <security>
            <authEncryption>
                <authentication>WPA2PSK</authentication>
                <encryption>AES</encryption>
                <useOneX>false</useOneX>
            </authEncryption>
            <sharedKey>
                <keyType>passPhrase</keyType>
                <protected>false</protected>
                <keyMaterial>{passphrase}</keyMaterial>
            </sharedKey>
        </security>
    </MSM>
    <MacRandomization xmlns="http://www.microsoft.com/networking/WLAN/profile/v3">
        <enableRandomization>false</enableRandomization>
    </MacRandomization>
</WLANProfile>'''


def list_devices():
    output = run(['adb', 'devices', '-l'], stdout=PIPE).stdout.decode('ascii').split('\r\n')
    devices = []
    for line in output[1:]:
        if line == '':
            continue
        # TODO add more information about the device
        devices.append(line.split()[0])
    return devices


def list_wifi_networks():
    if os.name == 'nt':
        output = run(['netsh', 'wlan', 'show', 'networks'], stdout=PIPE).stdout.decode('ascii').split('\r\n')
        networks = []
        for line in output:
            if line.startswith('SSID'):
                networks.append(line.split()[-1])
        return networks
    else:
        print('Failure: wifi network listing not support on %s' % os.name)


def connect_to_wifi_network(network, passphrase):
    if os.name == 'nt':
        # create and add profile
        with open('temp.xml', mode='w') as fh:
            fh.write(PROFILE_XML.format(ssid=network, passphrase=passphrase))
        run(['netsh', 'wlan', 'add', 'profile', 'filename="temp.xml"'], stdout=DEVNULL)
        os.remove('temp.xml')
        # try to connect and wait until we're connected
        while True:
            run(['netsh', 'wlan', 'connect', network], stdout=DEVNULL)
            sleep(2)
            output = run(['netsh', 'wlan', 'show', 'interfaces'], stdout=PIPE).stdout.decode('ascii').split('\r\n')
            for line in output:
                if ('SSID' in line) and (line.split()[-1] == network):
                    return
    else:
        print('Failure: wifi network listing not support on %s' % os.name)


if __name__ == '__main__':
    print('Connecting to device')
    devices = list_devices()
    if len(devices) == 0:
        print('Failure: please connect a device')
        sys.exit(-1)
    if len(devices) > 1:
        print('Failure: disconnect every device except for one')
        sys.exit(-1)
    print('Selected device %s' % devices[0])
    print('Restarting robot controller')
    if run(['adb', 'shell', 'am', 'force-stop', 'com.qualcomm.ftcrobotcontroller'], stdout=DEVNULL).returncode != 0:
        print('Failure: unable to restart robot controller')
        sys.exit(-1)
    if run(['adb', 'shell', 'am', 'start', '-n',
            'com.qualcomm.ftcrobotcontroller/org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity'],
           stdout=DEVNULL).returncode != 0:
        print('Failure: unable to start robot controller')
        sys.exit(-1)
    # TODO fix a problem where logcat doesn't pick up the passphrase because the app wasn't installed by this computer
    print('Scanning logcat for passphrase')
    passphrase = None
    with Popen(['adb', 'logcat'], stdout=PIPE, universal_newlines=True) as proc:
        for line in iter(proc.stdout.readline, ''):
            if 'passphrase' in line.lower():
                passphrase = line.split()[-1]
                break
        proc.kill()
    print('Got WiFi passphrase: %s' % passphrase)
    print('Available networks:')
    networks = list_wifi_networks()
    for i, network in enumerate(networks):
        print('%d: %s' % (i, network))
    network = networks[int(input('Select network: '))]
    connect_to_wifi_network(network, passphrase)
    print('Connecting over wireless ADB')
    run(['adb', 'tcpip', '5555'], stdout=DEVNULL)
    run(['adb', 'connect', '192.168.49.1:5555'], stdout=DEVNULL)
    print('You may disconnect the device now')
